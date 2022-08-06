package com.jtmnetwork.profile

import com.jtmnetwork.profile.core.usecase.repository.ProfileRepositoryIntegrationTest
import com.jtmnetwork.profile.data.service.ProfileServiceUnitTest
import com.jtmnetwork.profile.entrypoint.controller.ProfileControllerUnitTest
import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.junit.runners.Suite.SuiteClasses

@RunWith(Suite::class)
@SuiteClasses(value = [
    ProfileRepositoryIntegrationTest::class,

    ProfileServiceUnitTest::class,

    ProfileControllerUnitTest::class
])
class ProfileApplicationTestSuite