# 4. Save file processing state in permanent datastore

Date: 2021-04-29

## Status

Accepted

## Context

Currently, we do not have any transient data store available in production environment.
We do not have a dedicated SRE, due to which we want to keep components in ecosystem as few as possible.
As a result, any transient information, which should survive an application restart, has to be stored in a permanent data store.

## Decision

We will save all transient data in permanent data store.
Data will be routinely truncated from tables storing transient data.

## Consequences

Majority of data stored by service in permanent data store will be transient data.
Routine maintenance will now be required, which needs to be performed either manually, or be automated.