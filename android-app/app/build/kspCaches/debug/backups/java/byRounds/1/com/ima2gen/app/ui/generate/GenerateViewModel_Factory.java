package com.ima2gen.app.ui.generate;

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
  private final Provider<OpenAiApi> apiProvider;

  private final Provider<HistoryDao> historyDaoProvider;

  public GenerateViewModel_Factory(Provider<OpenAiApi> apiProvider,
      Provider<HistoryDao> historyDaoProvider) {
    this.apiProvider = apiProvider;
    this.historyDaoProvider = historyDaoProvider;
  }

  @Override
  public GenerateViewModel get() {
    return newInstance(apiProvider.get(), historyDaoProvider.get());
  }

  public static GenerateViewModel_Factory create(javax.inject.Provider<OpenAiApi> apiProvider,
      javax.inject.Provider<HistoryDao> historyDaoProvider) {
    return new GenerateViewModel_Factory(Providers.asDaggerProvider(apiProvider), Providers.asDaggerProvider(historyDaoProvider));
  }

  public static GenerateViewModel_Factory create(Provider<OpenAiApi> apiProvider,
      Provider<HistoryDao> historyDaoProvider) {
    return new GenerateViewModel_Factory(apiProvider, historyDaoProvider);
  }

  public static GenerateViewModel newInstance(OpenAiApi api, HistoryDao historyDao) {
    return new GenerateViewModel(api, historyDao);
  }
}
