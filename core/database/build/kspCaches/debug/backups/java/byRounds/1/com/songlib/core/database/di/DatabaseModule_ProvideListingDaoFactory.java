package com.songlib.core.database.di;

import com.songlib.core.database.AppDatabase;
import com.songlib.core.database.daos.ListingDao;
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
public final class DatabaseModule_ProvideListingDaoFactory implements Factory<ListingDao> {
  private final Provider<AppDatabase> dbProvider;

  private DatabaseModule_ProvideListingDaoFactory(Provider<AppDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public ListingDao get() {
    return provideListingDao(dbProvider.get());
  }

  public static DatabaseModule_ProvideListingDaoFactory create(Provider<AppDatabase> dbProvider) {
    return new DatabaseModule_ProvideListingDaoFactory(dbProvider);
  }

  public static ListingDao provideListingDao(AppDatabase db) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideListingDao(db));
  }
}
