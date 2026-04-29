package com.ima2gen.app.ui.gallery;

import com.ima2gen.app.data.local.db.HistoryDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.Providers;
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
public final class GalleryViewModel_Factory implements Factory<GalleryViewModel> {
  private final Provider<HistoryDao> historyDaoProvider;

  public GalleryViewModel_Factory(Provider<HistoryDao> historyDaoProvider) {
    this.historyDaoProvider = historyDaoProvider;
  }

  @Override
  public GalleryViewModel get() {
    return newInstance(historyDaoProvider.get());
  }

  public static GalleryViewModel_Factory create(
      javax.inject.Provider<HistoryDao> historyDaoProvider) {
    return new GalleryViewModel_Factory(Providers.asDaggerProvider(historyDaoProvider));
  }

  public static GalleryViewModel_Factory create(Provider<HistoryDao> historyDaoProvider) {
    return new GalleryViewModel_Factory(historyDaoProvider);
  }

  public static GalleryViewModel newInstance(HistoryDao historyDao) {
    return new GalleryViewModel(historyDao);
  }
}
