package com.apps.arbaelbarca.omrscanner.di

//@Module
//@InstallIn(SingletonComponent::class)
//object AppModule {
//
//    @Provides
//    fun provideBaseUrl() = ""
//
//
//    @Singleton
//    @Provides
//    fun provideOKHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
//        return OkHttpClient.Builder()
//            .addInterceptor(httpLoggingInterceptor)
//            .readTimeout(1200, TimeUnit.SECONDS)
//            .connectTimeout(1200, TimeUnit.SECONDS)
//            .build()
//
//    }
//
//    @Provides
//    @Singleton
//    fun provideRetrofitInstance(BASE_URL: String, okHttpClient: OkHttpClient): ApiService =
//        Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .client(okHttpClient)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(ApiService::class.java)
//
//    @Provides
//    @Singleton
//    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor =
//        HttpLoggingInterceptor()
//            .setLevel(HttpLoggingInterceptor.Level.BODY)
//
////    @Provides
////    fun provideViewModelModule() : ViewModelModule{
////        return ViewModelModule()
////    }
//
//}