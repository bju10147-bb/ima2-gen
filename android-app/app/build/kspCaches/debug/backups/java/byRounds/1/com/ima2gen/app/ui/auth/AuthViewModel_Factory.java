package com.ima2gen.app.ui.auth;

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
public final class AuthViewModel_Factory implements Factory<AuthViewModel> {
  private final Provider<SecureKeyStore> secureKeyStoreProvider;

  public AuthViewModel_Factory(Provider<SecureKeyStore> secureKeyStoreProvider) {
    this.secureKeyStoreProvider = secureKeyStoreProvider;
  }

  @Override
  public AuthViewModel get() {
    return newInstance(secureKeyStoreProvider.get());
  }

  public static AuthViewModel_Factory create(
      javax.inject.Provider<SecureKeyStore> secureKeyStoreProvider) {
    return new AuthViewModel_Factory(Providers.asDaggerProvider(secureKeyStoreProvider));
  }

  public static AuthViewModel_Factory create(Provider<SecureKeyStore> secureKeyStoreProvider) {
    return new AuthViewModel_Factory(secureKeyStoreProvider);
  }

  public static AuthViewModel newInstance(SecureKeyStore secureKeyStore) {
    return new AuthViewModel(secureKeyStore);
  }
}
