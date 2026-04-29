package com.ima2gen.app.ui.settings;

import com.ima2gen.app.data.local.SecureKeyStore;
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
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<SecureKeyStore> secureKeyStoreProvider;

  public SettingsViewModel_Factory(Provider<SecureKeyStore> secureKeyStoreProvider) {
    this.secureKeyStoreProvider = secureKeyStoreProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(secureKeyStoreProvider.get());
  }

  public static SettingsViewModel_Factory create(
      javax.inject.Provider<SecureKeyStore> secureKeyStoreProvider) {
    return new SettingsViewModel_Factory(Providers.asDaggerProvider(secureKeyStoreProvider));
  }

  public static SettingsViewModel_Factory create(Provider<SecureKeyStore> secureKeyStoreProvider) {
    return new SettingsViewModel_Factory(secureKeyStoreProvider);
  }

  public static SettingsViewModel newInstance(SecureKeyStore secureKeyStore) {
    return new SettingsViewModel(secureKeyStore);
  }
}
