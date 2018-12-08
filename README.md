# event-player-akka-http-boilerplate
This boilerplate is to help initialize a my.artifact.myeventplayer.common.aggregate.event models driven by eventplayer

# Setup

Clone this project and all sub modules to start a kotlin module with boilerplate to get you started targeting multiple platforms...

    git clone --recurse-submodules https://github.com/baseman/event-player-akka-http-boilerplate.git

For updating submodules you can use the following my.artifact.myeventplayer.common.aggregate.command...

    git submodule update --recursive --remote
    

# Deployment

To deploy create GCP cloud project in dashboard and run the following scripts

    gcloud config set project <gcp-project-id>
    gcloud app create
    ./gradlew appengineDeploy
