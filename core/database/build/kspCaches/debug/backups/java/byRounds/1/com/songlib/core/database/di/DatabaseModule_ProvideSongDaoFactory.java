package com.songlib.core.database.di;

import com.songlib.core.database.AppDatabase;
import com.songlib.core.database.daos.SongDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class DatabaseModule_ProvideSongDaoFactory implements Factory<SongDao> {
  private final Provider<AppDatabase> dbProvider;

  private DatabaseModule_ProvideSongDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public SongDao get() {
    return provideSongDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideSongDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideSongDaoFactory(dbProvider);
  }

  public static SongDao provideSongDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideSongDao(db));
  }
}
