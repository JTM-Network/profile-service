# Profile Service
This service is aimed to allow for monetisation by controlling the access to software & to store subscriptions pertaining to the software being used on the platform.

# Motivation
As the JTM Network portfolio of products grows, to remove duplication in profiles being created for each product. This service aims to generalise the process of access control & subscriptions across the network.

# Technologies Used
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Kotlin](https://kotlinlang.org)

# Features
- Stripe hook support.
- Paypal hook support.
- Profile fetching based on authorization platform:
   - [Auth0](https://auth0.com)
   - [Custom](https://github.com/JTM-Network/account-service)
