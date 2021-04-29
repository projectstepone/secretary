# 3. Sync file upload

Date: 2021-04-19

## Status

Accepted

## Context

File can be uploaded from user machine to a file server (SFTP, Amazon S3, etc), post which it can be picked up by service for processing.
Another approach is to pass file directly to service for processing.
We don't expect file size to exceed few MBs.

Asynchronous processing of file allows us to reprocess file in case of any transient issues with service.
 So user intervention is not required in case of most failures.

Synchronous processing of file reduces complexity of overall system.
Hence, it is faster to implement in service. 

## Decision

We will go ahead and use sync upload of file to service. This means that user will directly upload file from their machine to service, via frontend.

## Consequences

Sync processing of file is not robust against network-related issues.
Sync call for large files will have huge response time.
Hence, sync call may face timeout, enforced by load balancer or any other network entity involved in communication.

If this starts to become a problem, we can move to async processing in future. Logic of processing file will be kept independent of logic accepting file input in service. This should ease future transition to async flow.