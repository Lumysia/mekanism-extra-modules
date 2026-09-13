# Releasing

Releases are built and published by `.github/workflows/release.yml` when a valid version tag is pushed. The workflow creates the GitHub Release with the release JAR and its SHA-256 checksum, then publishes the same JAR to Modrinth and CurseForge in independent jobs.

## Repository configuration

Configure these values under **Settings > Secrets and variables > Actions**:

| Type | Name | Purpose |
| --- | --- | --- |
| Variable | `MODRINTH_PROJECT_ID` | Existing Modrinth project ID (`TfCrGUbp`) |
| Variable | `CURSEFORGE_PROJECT_ID` | Existing CurseForge project ID (`1604167`) |
| Secret | `MODRINTH_TOKEN` | Modrinth token with `VERSION_CREATE` and `PROJECT_WRITE` scopes |
| Secret | `CURSEFORGE_TOKEN` | CurseForge upload token authorized for the project |

Tokens must remain GitHub Actions secrets. Do not put token values in workflow files, logs, issue comments, or release notes. The Modrinth token needs `VERSION_CREATE` to publish artifacts and `PROJECT_WRITE` to synchronize the project description. Use tokens with only the required scopes and project access whenever the platform supports those restrictions.

The build job has read-only repository access and checks out without persisted credentials. A separate job with repository contents write access downloads the completed release bundle and creates or verifies the GitHub Release without checking out source or running Gradle. Platform publisher jobs receive write access only to commit statuses so they can record durable, tag-specific publication receipts. The README synchronization workflow receives read-only repository contents access.

## Creating a release

1. Update and review `minecraft_version` and `mod_version` in `gradle.properties` as part of a separate release-preparation change.
2. Ensure the release commit has passed the normal build workflow.
3. Create an annotated tag whose name is exactly `v${minecraft_version}-${mod_version}`. The workflow rejects lightweight tags and tags that do not resolve to the checked-out release commit.
4. Push that tag to GitHub.

For example, version `0.1.0-alpha.2` for Minecraft `1.21.1` must use tag `v1.21.1-0.1.0-alpha.2`. Alpha and beta versions are marked as prereleases on GitHub and use the matching release channel on Modrinth and CurseForge.

## Publication behavior

The release workflow publishes one NeoForge JAR for Minecraft 1.21.1 and Java 21. It declares Mekanism and Mekanism Tools as required dependencies and Draconic Evolution as optional on both distribution platforms. Release notes contain first-parent commits since the previous tag, while GitHub also retains its generated release notes behavior.

The GitHub Release step is safe to rerun. If the release already exists, its title, prerelease state, JAR, and checksum must match the rebuilt release. Missing assets are uploaded, conflicting assets or metadata fail the job instead of replacing an existing release, and an interrupted matching draft is published after its assets are repaired.

Modrinth and CurseForge run independently after the GitHub Release completes, so one platform can fail without republishing the other. Each successful upload records an audit commit status keyed by the tag and platform. Every rerun checks the real platform artifact before deciding whether to upload, regardless of any receipt. A version or filename collision with different content fails safely.

The publisher performs one upload attempt. Automatic upload retries are disabled because an interrupted upload can succeed remotely even when the client receives an error. Read-only preflight requests use bounded timeouts and retries. After a transient upload failure, rerun the failed job; its preflight determines whether an upload is still needed. If a newly uploaded CurseForge file is still being processed and is not visible to the preflight API, verify the project file list before rerunning the CurseForge job.

## Modrinth project description

`.github/workflows/sync-modrinth-readme.yml` copies `README.md` to the Modrinth project's long description whenever the README changes on `main`. It can also be run manually with **Actions > Sync Modrinth README > Run workflow**. The separate workflow keeps description failures and retries independent from artifact publication.

README images and repository files use absolute URLs under `https://raw.githubusercontent.com/Lumysia/mekanism-extra-modules/main/` or `https://github.com/Lumysia/mekanism-extra-modules/blob/main/` so they render from both GitHub and Modrinth. CurseForge description synchronization is intentionally not performed because its official upload API does not provide a project-description update operation.
