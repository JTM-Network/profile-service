package com.jtmnetwork.profile

import com.jtmnetwork.profile.core.usecase.repository.ProfileRepositoryIntegrationTest
import com.jtmnetwork.profile.core.usecase.repository.TokenRepositoryIntegrationTest
import com.jtmnetwork.profile.data.service.*
import com.jtmnetwork.profile.entrypoint.controller.*
import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.junit.runners.Suite.SuiteClasses

@RunWith(Suite::class)
@SuiteClasses(value = [
    ProfileRepositoryIntegrationTest::class,
    TokenRepositoryIntegrationTest::class,

    ProfileServiceUnitTest::class,
    SubscriptionServiceUnitTest::class,
    PermissionServiceUnitTest::class,
    PluginAccessServiceUnitTest::class,
    TokenServiceUnitTest::class,
    AuthServiceUnitTest::class,

    ProfileControllerUnitTest::class,
    SubscriptionControllerUnitTest::class,
    PermissionControllerUnitTest::class,
    PluginAccessControllerUnitTest::class,
    AuthControllerUnitTest::class,
    TokenControllerUnitTest::class
])
class ProfileApplicationTestSuite