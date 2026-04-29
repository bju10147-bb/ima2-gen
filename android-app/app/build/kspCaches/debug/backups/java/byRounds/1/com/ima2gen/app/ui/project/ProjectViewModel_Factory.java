package com.ima2gen.app.ui.project;

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
public final class ProjectViewModel_Factory implements Factory<ProjectViewModel> {
  private final Provider<HistoryDao> historyDaoProvider;

  public ProjectViewModel_Factory(Provider<HistoryDao> historyDaoProvider) {
    this.historyDaoProvider = historyDaoProvider;
  }

  @Override
  public ProjectViewModel get() {
    return newInstance(historyDaoProvider.get());
  }

  public static ProjectViewModel_Factory create(
      javax.inject.Provider<HistoryDao> historyDaoProvider) {
    return new ProjectViewModel_Factory(Providers.asDaggerProvider(historyDaoProvider));
  }

  public static ProjectViewModel_Factory create(Provider<HistoryDao> historyDaoProvider) {
    return new ProjectViewModel_Factory(historyDaoProvider);
  }

  public static ProjectViewModel newInstance(HistoryDao historyDao) {
    return new ProjectViewModel(historyDao);
  }
}
