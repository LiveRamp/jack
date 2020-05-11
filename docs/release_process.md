# Release Process

By convention the `master` branch is the active development branch.  The
assumption is there is only one active line of development at any time, so
`master` is also where releases are made.

## Developing and Releasing a New Patch Version

Prerequisite: The head of the `master` branch is ready to be released as a patch
update.

1. Take note of the current released version.

   This example shows how to list tags as versions in descending order.

   ```console
   $ git tag --list --sort='-version:refname'
   jack-1.0
   1.5.0
   1.4.2
   1.4.1
   1.4.0
   1.3.0
   1.2
   ```

2. Increment the patch value by 1 to get the next release version.

   Using the example above, the current release is "1.5.0" and incrementing the
   patch value gives "1.5.1".

3. Create an annotated git tag using the next release version.

   ```
   $ git tag -a -m 'Release 1.5.1' 1.5.1
   ```

   By creating an annotated tag (rather than a lightweight tag), the repository
   will contain a record of who tagged a release and when.

4. Push the newly created tag to GitHub.

   ```
   $ git push origin refs/tags/1.5.1
   Enumerating objects: 1, done.
   Counting objects: 100% (1/1), done.
   Writing objects: 100% (1/1), 164 bytes | 164.00 KiB/s, done.
   Total 1 (delta 0), reused 0 (delta 0)
   To github.com:LiveRamp/jack.git
    * [new tag]           1.5.1 -> 1.5.1
   ```

## Advancing the Minor or Major Version

As soon as it's decided that the next release will be a major or minor update,
the snapshot version in the repository should be updated.  Although it's not
required because snapshots are by definition unstable, it's nice to communicate
this to users sooner rather than later.

The bookkeeping in Maven for advancing the major or minor version is the same.

1. Decide on the new `MAJOR`.`MINOR` version.

   Minor updates should increment the existing minor value by one.  Major
   updates should increment the existing major value by one and set the minor
   version to 0.

2. Update the `revision` property in `com.liveramp:jack`'s POM.  Be sure to
   retain the `SNAPSHOT` qualifier.

   This is an example diff after changing the minor version from 5 to 6.

   ```
   $ git diff pom.xml
   diff --git a/pom.xml b/pom.xml
   index c3f2aff2..d5f28ca4 100644
   --- a/pom.xml
   +++ b/pom.xml
   @@ -37,7 +37,7 @@
        <db.user>root</db.user>
        <db.pass>""</db.pass>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
   -    <revision>1.5-SNAPSHOT</revision>
   +    <revision>1.6-SNAPSHOT</revision>
      </properties>

      <scm>
   ```

## Releasing a New Major or Minor Version

1. Verify that the `revision` property in `com.liveramp:jack` is consistent with
   the planned release.  If it is not, update it according to the steps above
   and commit those changes to `master` first.

2. Follow the steps from *Developing and Releasing a New Patch Version*, but
   skip to Step 3 and use the desired release version.
