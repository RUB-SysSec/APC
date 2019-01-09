# Change Log
All notable changes to this project will be documented in this file.
This project adheres to [Semantic Versioning](http://semver.org/).

## [Unreleased]
### Added
- Support for more visual properties

## [1.0.0] - 2019-01-15
### Added
- Initial version of the Android pattern classifier (APC)
- Main functionalities:
  * Verifying patterns for their validity (detect data collection issues)
  * Calculating scores based on visual pattern properties like
    * `starting node`
    * `length`
    * `intersections`
    * `knight moves`
    * `overlapping nodes`
  * Support for three Android unlock pattern strength meters proposed in the literature
    * [Andriotis et al. - 2014](https://link.springer.com/chapter/10.1007/978-3-319-07620-1_11)
    * [Sun et al. - 2014](https://www.sciencedirect.com/science/article/abs/pii/S2214212614001458)
    * [Song et al. - 2015](https://dl.acm.org/citation.cfm?id=2702365)
  * Support for larger grid sizes than 3x3, e.g., [4x4](https://dl.acm.org/citation.cfm?id=2818014)
  * Support to evaluate single patterns (`-p`) or text files containing a list of patterns (`-f`)
  * Support for custom delimiters different from ".", e.g., `0124678` instead of `0.1.2.4.6.7.8`

[Unreleased]:
[1.0.0]:
