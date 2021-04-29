# 2. Implement backend service in Java

Date: 2021-04-07

## Status

Accepted

## Context

As of now, bulk ingestion of data is supported in Statesman service (*state transition service*) using scripts.
To ingest data via these scripts, someone with access to relevant production machines has to run the scripts manually.

In an ideal scenario, operations team should be able to ingest this data to Statesman without access to production machines.
To enable this, a service is required, which implements file parsing, sanity checks and data ingestion to Statesman.

A backend service, along with suitable frontend is required for this. Developers available for this project have the option to implement backend service in any language of their choice.

The only constraint is to get service available as early as possible.

## Decision

Developers immediately available for this project are well-versed with writing backend in Java, using DropWizard framework.
Usage of Javascript with ExpressJS framework was evaluated.
However, learning curve required is considered significant.

Hence, we have decided to go ahead with DropWizard based backend service.

## Consequences

Future contributors to project need to make themselves familiar with DropWizard framework.

Some of the available developers are already familiar with DropWizard, so currently feedback is easily available.
Development of project will also be faster as a major learning curve is not involved in this project for current developer.