# Android Branding and Release Safety

## ANANVI phoenix identity

The app's vector brand asset is `app/src/main/res/drawable/ic_ai_logo.xml`. AndroidManifest uses it for both launcher and round icons; SplashScreen already uses the same resource. The Home screen now also displays the mark next to the product name. The artwork is a scalable red phoenix on a dark field and requires no remote asset loading.

## CI and publishing boundaries

The Android GitHub Actions workflow now:
- Runs unit tests and builds the debug APK for pull requests, pushes and manual dispatch.
- Uploads the debug APK as a workflow artifact.
- Uses read-only repository permissions.
- Does not create GitHub Releases or publish artifacts externally.

Release publishing must be a separate, explicitly approved operation.

## Signing

The release build type no longer silently uses the debug signing key. Without a protected release signing configuration, release APKs are unsigned and are not suitable for installation as upgrades or Play Store distribution.

Before a production release:
1. Recover and verify the existing production upload/release keystore, if one exists.
2. Keep keystore files out of Git and ordinary CI artifacts.
3. Store signing material only in protected GitHub Actions secrets or an approved secure signing service.
4. Configure release signing without changing the existing application ID or package identity.
5. Verify the signing certificate fingerprint matches the currently distributed app before publishing an upgrade.
6. Build, test and review the signed artifact, then obtain explicit release approval.

Do not generate a replacement keystore until the existing signing identity has been checked. Losing or replacing the signing identity can prevent users from upgrading an already-installed app.
