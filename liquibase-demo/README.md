# Liquibase Commands

The Liquibase Maven Plugin has a number of commands to help test and validate the change sets are implemented correctly.

To see a list of commands use the `help` goal.

```bash
$ ./mvnw -pl :liquibase-demo liquibase:help
```

## Validate

```bash
$ ./mvnw -pl :liquibase-demo liquibase:validate -Dliquibase.url=jdbc:h2:mem:test
```

### Generate SQL that Liquibase will execute

The Plugin can generate the SQL that Liquibase will execute when run against a database.

```bash
$ ./mvnw -pl :liquibase-demo liquibase:updateSQL -Dliquibase.url=jdbc:h2:mem:test
```

*NOTE:* The output will be located in `targer/liquibase/migrate.sql`

## Rollback

Liquibase will attempt to generate appropriate rollback scripts. However, the rollback can be directly supplied in the changelog.

*NOTE:* See `src/main/resources/db/changelog/db.changelog-master.yaml`

### Rollback Testing

```bash
$ ./mvnw -pl :liquibase-demo liquibase:updateTestingRollback -Dliquibase.url=jdbc:h2:mem:test
```

### Generate a Rollback script

There are a number of rollback types, however rolling back to one prior change set is probably most relevant.

```bash
$ ./mvnw -pl :liquibase-demo liquibase:rollbackSQL -Dliquibase.rollbackCount=1 -Dliquibase.url=jdbc:h2:mem:test
```
