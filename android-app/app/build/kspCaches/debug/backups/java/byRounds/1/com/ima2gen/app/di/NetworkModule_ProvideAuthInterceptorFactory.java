package com.ima2gen.app.di;

import com.ima2gen.app.data.local.SecureKeyStore;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.Providers;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import okhttp3.Interceptor;

@ScopeMetadata("javax.inject.Singleton")
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
public final class NetworkModule_ProvideAuthInterceptorFactory implements Factory<Interceptor> {
  private final Provider<SecureKeyStore> secureKeyStoreProvider;

  public NetworkModule_ProvideAuthInterceptorFactory(
      Provider<SecureKeyStore> secureKeyStoreProvider) {
    this.secureKeyStoreProvider = secureKeyStoreProvider;
  }

  @Override
  public Interceptor get() {
    return provideAuthInterceptor(secureKeyStoreProvider.get());
  }

  public static NetworkModule_ProvideAuthInterceptorFactory create(
      javax.inject.Provider<SecureKeyStore> secureKeyStoreProvider) {
    return new NetworkModule_ProvideAuthInterceptorFactory(Providers.asDaggerProvider(secureKeyStoreProvider));
  }

  public static NetworkModule_ProvideAuthInterceptorFactory create(
      Provider<SecureKeyStore> secureKeyStoreProvider) {
    return new NetworkModule_ProvideAuthInterceptorFactory(secureKeyStoreProvider);
  }

  public static Interceptor provideAuthInterceptor(SecureKeyStore secureKeyStore) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideAuthInterceptor(secureKeyStore));
  }
}
