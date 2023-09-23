# Spring Trading App :: Marketplace

> [!NOTE]
> This is one of many components from
> [Spring Trading App](https://github.com/alexandreroman/sta).

This component provides a REST API for trading agents.
Users get a list of stocks, and can place bids:
a realtime leaderboard is updated with user balances.

## Running this component on your workstation

Run a Docker daemon on your workstation before starting the app.
Spring Boot will take care of running Docker Compose in order to spin up
a local PostgreSQL database instance.

Use this command to run this component on your workstation:

```shell
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

The app is available at http://localhost:8081.

Here are some available endpoints:

- `/api/v1/stocks` to get a list of stocks
- `/api/v1/leaderboard` to get a realtime leaderboard
- `/api/v1/users/{user}` to get user details

More endpoints are defined: see source code for details.

Application configuration is defined in `src/main/resources/application.yaml`.
You may want to override this configuration in
`src/main/resources/application-dev.yaml`:

```yaml
app:
  stocks:
    frozen: false
    refresh-rate: 4000
    initial-values:
      vmw: 150
      googl: 130
      aapl: 180
      amzn: 130
      meta: 290
      msft: 330
    updaters:
      vmw: default
      googl: around-initial
      aapl: steady-growth
      amzn: default
      meta: default
      msft: penny-stock
```

Please note that some endpoints require a JWT token 
(included in the HTTP header `Authorization: Bearer $token`).

Those JWT tokens are verified against an OAuth2 Resource Server (where you initially create JWT tokens).

A default OAuth2 resource server is defined in the configuration:

```yaml
spring:
  security:
    oauth2:
      resource-server:
        jwt:
          issuer-uri: https://login.sso.az.run.withtanzu.com
```

## Deploying with VMware Tanzu Application Platform

Use this command to deploy this component to your favorite Kubernetes cluster:

```shell
tanzu apps workload apply -f config/workload.yaml
```

The platform will take care of building, testing and deploying this component.

You need a database to run this app. Run this command to create a PostgreSQL database:

```shell
tanzu service class-claim create sta-marketplace-db --class postgresql-unmanaged
```

This component also loads some configuration from a
[Git repository](https://github.com/alexandreroman/sta-config).

Run this command to create a Kubernetes `Secret` out of this Git repository,
which will be used by the component at runtime:

```shell
kubectl apply -f config/app-operator
```

Run this command to get deployment status:

```shell
tanzu apps workload get sta-marketplace
```
