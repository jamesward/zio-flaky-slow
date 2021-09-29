ZIO Flaky & Slow
----------------

```
./sbt test

./sbt "runMain Flaky"

./sbt "runMain Slow"

./sbt "runMain WebApp"
```

- [localhost:8080/flaky](http://localhost:8080/flaky)
- [localhost:8080/slow](http://localhost:8080/slow)

## Todo

- Nicer test response value extraction
- Scala 3 vararg significant indentation in tests
- Server doesn't shutdown on ctrl-c
- 