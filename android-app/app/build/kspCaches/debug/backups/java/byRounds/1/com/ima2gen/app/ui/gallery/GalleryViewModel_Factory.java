package com.ima2gen.app.ui.gallery;

import androidx.lifecycle.SavedStateHandle;
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

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  public GalleryViewModel_Factory(Provider<HistoryDao> historyDaoProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    this.historyDaoProvider = historyDaoProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public GalleryViewModel get() {
    return newInstance(historyDaoProvider.get(), savedStateHandleProvider.get());
  }

  public static GalleryViewModel_Factory create(
      javax.inject.Provider<HistoryDao> historyDaoProvider,
      javax.inject.Provider<SavedStateHandle> savedStateHandleProvider) {
    return new GalleryViewModel_Factory(Providers.asDaggerProvider(historyDaoProvider), Providers.asDaggerProvider(savedStateHandleProvider));
  }

  public static GalleryViewModel_Factory create(Provider<HistoryDao> historyDaoProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new GalleryViewModel_Factory(historyDaoProvider, savedStateHandleProvider);
  }

  public static GalleryViewModel newInstance(HistoryDao historyDao,
      SavedStateHandle savedStateHandle) {
    return new GalleryViewModel(historyDao, savedStateHandle);
  }
}
