package com.songlib.core.database.di;

import com.songlib.core.database.AppDatabase;
import com.songlib.core.database.daos.BookDao;
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
public final class DatabaseModule_ProvideBookDaoFactory implements Factory<BookDao> {
  private final Provider<AppDatabase> dbProvider;

  private DatabaseModule_ProvideBookDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public BookDao get() {
    return provideBookDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideBookDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideBookDaoFactory(dbProvider);
  }

  public static BookDao provideBookDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideBookDao(db));
  }
}
