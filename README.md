RFID
====

Currently the bibs RFID build holds both the bibs server and the rfid server (though this may not be permanent).

Branch Structure:
Master:
  - This branch holds releases and release candidates

Develop:
  - This branch holds merges and commits of new development features

Build types:
  - All build types have a typename visible in /src/main/java/com/bibsmobile/util/BuildTypeUtil.java
  - These comes along with a set of properties that dictate what is shown in the UI:
    -- licensing -- whether the build uses license tokens for reads
    -- rfid -- whether this is an rfid included build
    -- registration -- whether this is the live registration build for accepting payments/results

Build Servers:
 - Carpunch: This is the semistable engineering build server. This builds off of the develop branch
 - Sakebomb: This is a private build server for so/galen. Builds are triggered from their branches
