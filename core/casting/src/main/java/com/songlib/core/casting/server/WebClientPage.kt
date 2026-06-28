package com.songlib.core.casting.server

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
          #header {
            display: none;
            flex-direction: column;
            align-items: center;
            text-align: center;
            gap: 4px;
            padding: 28px 56px 0;
          }
          #title-text {
            font-size: clamp(20px, 3.4vw, 30px);
            font-weight: 700;
            color: #f9fafb;
            line-height: 1.25;
            max-width: 100%;
          }
          #book-text {
            font-size: 13px;
            font-weight: 600;
            letter-spacing: .06em;
            text-transform: uppercase;
            color: #9ca3af;
          }
          #book-text:empty {
            display: none;
          }
          #idle-screen {
            flex: 1;
            display: flex;
            align-items: center;
            justify-content: center;
            flex-direction: column;
            gap: 20px;
            text-align: center;
            padding: 24px;
          }
          #idle-icon {
            animation: idle-breathe 2.4s ease-in-out infinite;
          }
          #idle-icon svg {
            display: block;
          }
          @keyframes idle-breathe {
            0%, 100% { transform: scale(1); opacity: .85; }
            50% { transform: scale(1.06); opacity: 1; }
          }
          #idle-screen h1 {
            font-size: 18px;
            font-weight: 600;
            color: #9ca3af;
            margin: 0;
          }
          #slide-stage {
            flex: 1;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 32px 32px 96px;
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
        </style>
        </head>
        <body>
          <div id="app">
            <div id="status-dot"></div>

            <div id="header">
              <div id="title-text"></div>
              <div id="book-text"></div>
            </div>

            <div id="idle-screen">
              <div id="idle-icon">
                <svg width="64" height="64" viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <rect width="64" height="64" rx="18" fill="#FFB951"/>
                  <rect x="28" y="10" width="22" height="6" rx="3" fill="#291800"/>
                  <rect x="28" y="16" width="4" height="28" fill="#291800"/>
                  <rect x="46" y="16" width="4" height="24" fill="#291800"/>
                  <ellipse cx="30" cy="44" rx="7" ry="5" fill="#291800"/>
                  <ellipse cx="48" cy="40" rx="7" ry="5" fill="#291800"/>
                </svg>
              </div>
              <h1>Waiting for a presentation&hellip;</h1>
            </div>

            <div id="slide-stage" style="display:none">
              <div id="verses"></div>
              <div id="indicators"></div>
            </div>
          </div>

          <script>
            (function () {
              var statusDot = document.getElementById('status-dot');
              var header = document.getElementById('header');
              var titleText = document.getElementById('title-text');
              var bookText = document.getElementById('book-text');
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
                header.style.display = 'none';
                idleScreen.style.display = 'flex';
                slideStage.style.display = 'none';
                lastSignature = null;
              }

              function showSlide(state) {
                header.style.display = 'flex';
                idleScreen.style.display = 'none';
                slideStage.style.display = 'flex';

                titleText.textContent = state.title || '';
                bookText.textContent = state.book || '';

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
