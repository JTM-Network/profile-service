package com.jtmnetwork.profile

import com.jtmnetwork.profile.core.usecase.repository.ProfileRepositoryIntegrationTest
import com.jtmnetwork.profile.data.service.PermissionServiceUnitTest
import com.jtmnetwork.profile.data.service.PluginAccessServiceUnitTest
import com.jtmnetwork.profile.data.service.ProfileServiceUnitTest
import com.jtmnetwork.profile.data.service.SubscriptionServiceUnitTest
import com.jtmnetwork.profile.entrypoint.controller.PermissionControllerUnitTest
import com.jtmnetwork.profile.entrypoint.controller.PluginAccessControllerUnitTest
import com.jtmnetwork.profile.entrypoint.controller.ProfileControllerUnitTest
import com.jtmnetwork.profile.entrypoint.controller.SubscriptionControllerUnitTest
import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.junit.runners.Suite.SuiteClasses

@RunWith(Suite::class)
@SuiteClasses(value = [
    ProfileRepositoryIntegrationTest::class,

    ProfileServiceUnitTest::class,
    SubscriptionServiceUnitTest::class,
    PermissionServiceUnitTest::class,
    PluginAccessServiceUnitTest::class,

    ProfileControllerUnitTest::class,
    SubscriptionControllerUnitTest::class,
    PermissionControllerUnitTest::class,
    PluginAccessControllerUnitTest::class
])
class ProfileApplicationTestSuite