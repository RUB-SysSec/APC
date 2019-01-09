APC: Android (Unlock) Pattern Classifier
========================================

APC is a classifier for Android unlock patterns written in Kotlin. It calculates scores derived from visual properties that are used by various Android unlock pattern strength meters to estimate the strength of a given pattern. If you are interested in the details on how accurate current Android unlock pattern strength meter proposals are and how a Markov model-based pattern strength meter improves on their accuracy, please refer to [On the In-Accuracy and Influence of Android Pattern Strength Meters](https://www.mobsec.ruhr-uni-bochum.de/forschung/veroeffentlichungen/accuracy-android-pattern-strength-meters/).

  * Andriotis et al. - 2014 - [Complexity Metrics and User Strength Perceptions of the Pattern-Lock Graphical Authentication Method](https://link.springer.com/chapter/10.1007/978-3-319-07620-1_11)
  * Sun et al. - 2014 - [Dissecting Pattern Unlock: The Effect of Pattern Strength Meter on Pattern Selection](https://www.sciencedirect.com/science/article/abs/pii/S2214212614001458)
  * Song et al. - 2015 - [On the Effectiveness of Pattern Lock Strength Meters: Measuring the Strength of Real World Pattern Locks](https://dl.acm.org/citation.cfm?id=2702365)

User Guide
-----------

This software requires Java.

`$ java -jar apc.jar -p 0.1.2.5.8`
