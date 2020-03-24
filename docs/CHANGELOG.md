# Change Log
All notable changes to this project will be documented in this file.
This project adheres to [Semantic Versioning](http://semver.org/).

## [1.0.3] - 2019-07-24
### Fixed
- Code cleanup and polishing

## [1.0.2] - 2019-07-17
### Fixed
- Decimal representation of scores

## [1.0.1] - 2019-04-07
### Fixed
- Better self-testing

## [1.0.0] - 2019-01-15
### Added
- Initial version of the Android pattern classifier (APC)
- Main functionalities:
  * Verifying patterns for their validity (detect data collection issues)
  * Calculating scores based on visual pattern properties like
    * `1A: Starting Nodes`
    * `1B: Minimum Length`
    * `1C: Direction Changes`
    * `1D: Knight Moves`
    * `1E: Overlapping Nodes`
    * `2A: Length`
    * `2B: Physical Length`
    * `2C: Intersections`
    * `2D: Overlapping Segments`
    * `3A: Length in Maximum Norm`
    * `3B: Intersections (Restricted)`
    * `3C: Ratio of Non-Repeated Segments`
  * Support for three Android unlock pattern strength meters proposed in the literature
    * [Andriotis et al. - 2014](https://link.springer.com/chapter/10.1007/978-3-319-07620-1_11)
    * [Sun et al. - 2014](https://www.sciencedirect.com/science/article/abs/pii/S2214212614001458)
    * [Song et al. - 2015](https://dl.acm.org/citation.cfm?id=2702365)
  * Support for larger grid sizes than 3x3, e.g., [4x4](https://dl.acm.org/citation.cfm?id=2818014)
  * Support to evaluate single patterns (`-p`) or text files containing a list of patterns (`-f`)
  * Support for custom delimiters different from ".", e.g., `0124678` instead of `0.1.2.4.6.7.8`