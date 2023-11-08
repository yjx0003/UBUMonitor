<a href="README_es.md" >
<img align="right" src="src/main/resources/img/countries_flags/ES.png">
</a>

# UBUMonitor - Student Clustering Module
[![GitHub release](https://img.shields.io/github/release/yjx0003/UBUMonitor.svg)](https://github.com/yjx0003/UBUMonitor/releases/)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/f79d51e496b3495690aa6480269536b8)](https://www.codacy.com/app/yjx0003/UBUMonitor?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=yjx0003/UBUMonitor&amp;utm_campaign=Badge_Grade)

## Updatable version
Updatable version of the UBUMonitor application is available [here](https://github.com/yjx0003/UBUMonitorLauncher/releases)


## Student Clustering Module in UBUMonitor for the Moodle platform
### Description:

This module of UBUMonitor empowers educational researcher to fully implement a clustering process with educational data This application is aimed at teachers who use Moodle services.

<p align="center"><img height="300" src="https://ubumonitordocs.readthedocs.io/en/latest/_images/clustering_2d.png" />

Once the researcher has validated and chosen the Moodle course to be analysed, they can set up their analysis to create groups of learners based on their interactions and grades with the resources and activities available in the particular course. To this end, the clustering module includes the following features that match the different stages of the Data Clustering Process:

 - **Data preparation and cleaning**. Select the students (instances) and select the course components, their grades or activity completion information (features).
 - **Clustering identification**. Select a clustering algorithm by specifying the parameters of the number of groups, choosing a distance measure and filtering the features. Run the selected algorithm.
 - **Cluster evaluation and validity.** Validate the results of the clustering algorithm: checking the assignment of students to groups, visually checking the cluster properties and searching for the optimal number of clusters.
 - **Results application** by tagging student groups manually inside Moodle LMS and conducting teaching actions on each group. 

## Cite this
[Cite this](https://www.mdpi.com/2079-9292/11/6/954#cite)

## User's manual:
*https://ubumonitordocs.readthedocs.io/en/latest/clustering/index.html

## Dependencies:
The application requires **Java 8**.
External **JavaScript** libraries are included in  [resources/graphics/lib/](resources/graphics/lib/).
More details regarding Java and Javascript dependencies can be found in the [root folder of the UBUMonitor repository](https://commons.apache.org/proper/commons-csv/)
  
## Authors
- Xing Long Ji
- Yi Peng Ji
- Raúl Marticorena Sánchez
- Carlos Pardo Aguilar

## Acknlowledgments
We sincerely thanks to the Vice Chancellorship for Teaching Staff at the University of Burgos for their financial support to the DIGIT educational innovation group in promoting excellence in teaching and learning.

## License
This project is licensed under the MIT license - see the [LICENSE](LICENSE) file for details.
