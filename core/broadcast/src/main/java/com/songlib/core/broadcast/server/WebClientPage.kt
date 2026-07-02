package com.songlib.core.broadcast.server

/**
 * A single self-contained page (inline CSS + JS, no external requests) served
 * at "/". Keeping it as one file means the embedded server only needs one
 * route for content, plus the "/ws" WebSocket route that drives it.
 */
object WebClientPage {

    val html: String = """
        <!DOCTYPE html>
        <html lang="en">
        <head>
        <meta charset="utf-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <title>SongLib — Presenter Mirror</title>
        <style>
          :root {
            color-scheme: dark;
          }
          * { box-sizing: border-box; }
          html, body {
            margin: 0;
            height: 100%;
            background: #0b0c10;
            font-family: -apple-system, Roboto, "Segoe UI", Helvetica, Arial, sans-serif;
            overflow: hidden;
          }
          #app {
            position: fixed;
            inset: 0;
            display: flex;
            flex-direction: column;
          }
          #status-dot {
            position: absolute;
            top: 14px;
            right: 16px;
            width: 10px;
            height: 10px;
            border-radius: 50%;
            background: #ef4444;
            box-shadow: 0 0 0 0 rgba(239,68,68,.6);
            transition: background .25s;
            z-index: 10;
          }
          #status-dot.connected {
            background: #22c55e;
          }
          #title-pill {
            position: absolute;
            top: 14px;
            left: 16px;
            max-width: 70%;
            color: #9ca3af;
            font-size: 13px;
            letter-spacing: .02em;
            text-transform: uppercase;
            font-weight: 600;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
            z-index: 10;
          }
          #idle-screen {
            flex: 1;
            display: flex;
            align-items: center;
            justify-content: center;
            flex-direction: column;
            gap: 18px;
            color: #6b7280;
            text-align: center;
            padding: 24px;
          }
          #idle-screen .dot-pulse {
            width: 14px;
            height: 14px;
            border-radius: 50%;
            background: #374151;
            animation: pulse 1.6s ease-in-out infinite;
          }
          @keyframes pulse {
            0%, 100% { transform: scale(1); opacity: .5; }
            50% { transform: scale(1.6); opacity: 1; }
          }
          #idle-screen h1 {
            font-size: 20px;
            font-weight: 600;
            color: #d1d5db;
            margin: 0;
          }
          #idle-screen p {
            font-size: 14px;
            margin: 0;
            max-width: 360px;
            line-height: 1.5;
          }
          #slide-stage {
            flex: 1;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 64px 32px 96px;
            position: relative;
          }
          .verse {
            display: none;
            color: #f9fafb;
            font-size: clamp(22px, 5vw, 52px);
            line-height: 1.35;
            text-align: center;
            font-weight: 600;
            white-space: pre-wrap;
            max-width: 1000px;
          }
          .verse.active {
            display: block;
            animation: fade .25s ease-out;
          }
          @keyframes fade {
            from { opacity: 0; transform: translateY(6px); }
            to { opacity: 1; transform: translateY(0); }
          }
          #indicators {
            position: absolute;
            bottom: 28px;
            left: 0;
            right: 0;
            display: flex;
            justify-content: center;
            gap: 8px;
            flex-wrap: wrap;
            padding: 0 16px;
          }
          .indicator {
            min-width: 22px;
            height: 22px;
            padding: 0 6px;
            border-radius: 11px;
            background: #1f2937;
            color: #9ca3af;
            font-size: 11px;
            font-weight: 700;
            display: flex;
            align-items: center;
            justify-content: center;
          }
          .indicator.active {
            background: #f9fafb;
            color: #111827;
          }
          #app.hidden-stage #slide-stage { display: none; }
          #app.hidden-idle #idle-screen { display: none; }
        </style>
        </head>
        <body>
          <div id="app" class="hidden-stage">
            <div id="status-dot"></div>
            <div id="title-pill"></div>

            <div id="idle-screen">
              <div class="dot-pulse"></div>
              <h1>Waiting for a presentation&hellip;</h1>
              <p>Open a song or draft on the SongLib presenter screen on the phone and it will appear here automatically.</p>
            </div>

            <div id="slide-stage" style="display:none">
              <div id="verses"></div>
              <div id="indicators"></div>
            </div>
          </div>

          <script>
            (function () {
              var app = document.getElementById('app');
              var statusDot = document.getElementById('status-dot');
              var titlePill = document.getElementById('title-pill');
              var idleScreen = document.getElementById('idle-screen');
              var slideStage = document.getElementById('slide-stage');
              var versesEl = document.getElementById('verses');
              var indicatorsEl = document.getElementById('indicators');

              var lastSignature = null;
              var socket = null;
              var retryDelay = 1000;

              function escapeHtml(str) {
                return str
                  .replace(/&/g, '&amp;')
                  .replace(/</g, '&lt;')
                  .replace(/>/g, '&gt;');
              }

              function showIdle() {
                idleScreen.style.display = 'flex';
                slideStage.style.display = 'none';
                titlePill.textContent = '';
                lastSignature = null;
              }

              function showSlide(state) {
                idleScreen.style.display = 'none';
                slideStage.style.display = 'flex';
                titlePill.textContent = state.title || '';

                var signature = state.title + '|' + state.verses.join('\u0001');
                if (signature !== lastSignature) {
                  lastSignature = signature;
                  versesEl.innerHTML = state.verses
                    .map(function (verse, i) {
                      return '<div class="verse" data-i="' + i + '">' +
                        escapeHtml(verse).replace(/\n/g, '<br>') +
                        '</div>';
                    })
                    .join('');
                  indicatorsEl.innerHTML = (state.indicators || []).map(function (label, i) {
                    return '<div class="indicator" data-i="' + i + '">' + escapeHtml(label) + '</div>';
                  }).join('');
                }

                var verseNodes = versesEl.children;
                for (var i = 0; i < verseNodes.length; i++) {
                  verseNodes[i].classList.toggle('active', i === state.currentIndex);
                }
                var indicatorNodes = indicatorsEl.children;
                for (var j = 0; j < indicatorNodes.length; j++) {
                  indicatorNodes[j].classList.toggle('active', j === state.currentIndex);
                }
              }

              function render(state) {
                if (!state || state.type === 'idle') {
                  showIdle();
                } else if (state.type === 'slide') {
                  showSlide(state);
                }
              }

              function connect() {
                var protocol = location.protocol === 'https:' ? 'wss://' : 'ws://';
                socket = new WebSocket(protocol + location.host + '/ws');

                socket.onopen = function () {
                  statusDot.classList.add('connected');
                  retryDelay = 1000;
                };

                socket.onmessage = function (event) {
                  try {
                    render(JSON.parse(event.data));
                  } catch (e) {
                    // ignore malformed frames
                  }
                };

                socket.onclose = scheduleReconnect;
                socket.onerror = scheduleReconnect;
              }

              function scheduleReconnect() {
                statusDot.classList.remove('connected');
                if (socket) {
                  socket.onclose = null;
                  socket.onerror = null;
                }
                setTimeout(connect, retryDelay);
                retryDelay = Math.min(retryDelay * 1.5, 8000);
              }

              showIdle();
              connect();
            })();
          </script>
        </body>
        </html>
    """.trimIndent()
}
