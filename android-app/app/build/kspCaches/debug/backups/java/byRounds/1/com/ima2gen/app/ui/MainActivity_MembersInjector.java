package com.ima2gen.app.ui;

import com.ima2gen.app.data.local.SecureKeyStore;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;

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
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<SecureKeyStore> secureKeyStoreProvider;

  public MainActivity_MembersInjector(Provider<SecureKeyStore> secureKeyStoreProvider) {
    this.secureKeyStoreProvider = secureKeyStoreProvider;
  }

  public static MembersInjector<MainActivity> create(
      Provider<SecureKeyStore> secureKeyStoreProvider) {
    return new MainActivity_MembersInjector(secureKeyStoreProvider);
  }

  public static MembersInjector<MainActivity> create(
      javax.inject.Provider<SecureKeyStore> secureKeyStoreProvider) {
    return new MainActivity_MembersInjector(Providers.asDaggerProvider(secureKeyStoreProvider));
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectSecureKeyStore(instance, secureKeyStoreProvider.get());
  }

  @InjectedFieldSignature("com.ima2gen.app.ui.MainActivity.secureKeyStore")
  public static void injectSecureKeyStore(MainActivity instance, SecureKeyStore secureKeyStore) {
    instance.secureKeyStore = secureKeyStore;
  }
}
