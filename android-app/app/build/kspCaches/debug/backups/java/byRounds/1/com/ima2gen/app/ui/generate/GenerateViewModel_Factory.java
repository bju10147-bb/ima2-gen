package com.ima2gen.app.ui.generate;

import androidx.lifecycle.SavedStateHandle;
import com.ima2gen.app.data.api.OpenAiApi;
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
public final class GenerateViewModel_Factory implements Factory<GenerateViewModel> {
  private final Provider<HistoryDao> historyDaoProvider;

  private final Provider<OpenAiApi> openAiApiProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  public GenerateViewModel_Factory(Provider<HistoryDao> historyDaoProvider,
      Provider<OpenAiApi> openAiApiProvider, Provider<SavedStateHandle> savedStateHandleProvider) {
    this.historyDaoProvider = historyDaoProvider;
    this.openAiApiProvider = openAiApiProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public GenerateViewModel get() {
    return newInstance(historyDaoProvider.get(), openAiApiProvider.get(), savedStateHandleProvider.get());
  }

  public static GenerateViewModel_Factory create(
      javax.inject.Provider<HistoryDao> historyDaoProvider,
      javax.inject.Provider<OpenAiApi> openAiApiProvider,
      javax.inject.Provider<SavedStateHandle> savedStateHandleProvider) {
    return new GenerateViewModel_Factory(Providers.asDaggerProvider(historyDaoProvider), Providers.asDaggerProvider(openAiApiProvider), Providers.asDaggerProvider(savedStateHandleProvider));
  }

  public static GenerateViewModel_Factory create(Provider<HistoryDao> historyDaoProvider,
      Provider<OpenAiApi> openAiApiProvider, Provider<SavedStateHandle> savedStateHandleProvider) {
    return new GenerateViewModel_Factory(historyDaoProvider, openAiApiProvider, savedStateHandleProvider);
  }

  public static GenerateViewModel newInstance(HistoryDao historyDao, OpenAiApi openAiApi,
      SavedStateHandle savedStateHandle) {
    return new GenerateViewModel(historyDao, openAiApi, savedStateHandle);
  }
}
