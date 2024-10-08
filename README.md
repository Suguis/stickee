# Stickee

[![Tests 🧪](https://github.com/siesta-cat/stickee/actions/workflows/tests.yml/badge.svg)](https://github.com/siesta-cat/stickee/actions/workflows/tests.yml)
![GitHub Tag](https://img.shields.io/github/v/tag/siesta-cat/stickee?label=version)

Stickee is a simple and secure text storage application that allows you to post sensitive information and generate a temporary link to share it. Stickee focuses on security and is designed to be easily hosted on a private server, giving you control over your data.

## Try it

You use our **hosted Stickee instance** [here](https://stickee.siesta.cat), or deploy it yourself using the instructions below.

## Roadmap

As for now, the project has very basic features. Here's what's planned for future releases:

- [x] Choose when a note will be deleted
- [ ] Allow note deletion
- [x] Enhanced view with a fancy UI that shows details of the node, such as the time when the note will be deleted

## Deploying with Docker compose

You can read the details of how to pull the Docker image [here](https://github.com/siesta-cat/stickee/pkgs/container/stickee). Also, you can take a look to the [example docker compose file](https://github.com/siesta-cat/stickee/blob/main/example-compose.yaml).

## Deploying with Kubernetes

If you are using k8s, take a look to the [example k8s manifest](https://github.com/siesta-cat/stickee/blob/main/k8s-manifest.yaml).
