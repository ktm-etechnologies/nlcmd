# Natural language phrase matching for Android

Android supports a number of voice control features, but enabling an individual
app for fine grained voice control is not straight forward. This library helps to
achieve that.

Features include
* Fast and efficient markov-chain based matching.
* Simple API for training markov chains from an string array of possible
  commands variations or loading models from JSON data.
* Support for generic terms such as locations or objects, such that they
  don't need to be enumerated in the training data.
* Support for creating [Graphviz Dot](https://www.graphviz.org) representations
  of models for visualization and debugging

The code is in proof-of-concept stage and feedback is welcome. For a snapshot
of the documentation please refer to https://ktm-technologies.github.io/markov-phrase-matching-android-doc/
