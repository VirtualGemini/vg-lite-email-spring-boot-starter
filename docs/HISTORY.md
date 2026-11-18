# Email starter migration history

`main` was created from the original repository's `master` tip:
`ad1da2884493a8c631a2ec222efc17ac71c5c1d1`.
All original commits remain unchanged and reachable from `main`. `master` retains the previous implementation.

The new implementation comes from `VirtualGemini/velox-web-pro` at
`backend/velox-framework/velox-email-spring-boot-starter`.
The source working tree had no changes in this module; unrelated uncommitted work was not imported or modified.

Velox imported its backend as a Git subtree. The original backend tip is
`5262d14a9f8ce6946933506086b11613418d744c`, the second parent of
`b073152ac32666eaf4325bb8f11b96060fe6277b`.
The module files at that tip were verified byte-for-byte against the current Velox module: 53 files, no differences.

## Extraction

Filtering was performed in a temporary clone, without rewriting either original repository.
The complete email directory lineage was retained:

1. `architecture-pro-email/`
2. `velox-email/`
3. `velox-framework/velox-email-starter/`
4. `velox-framework/velox-email-spring-boot-starter/`

These prefixes were mapped to the repository root. Commits with no remaining email changes were pruned, yielding 14 module commits. Authors, committers, timestamps, and messages were preserved. Tree paths and parent graphs changed during extraction, so the extracted commits have new IDs. Pure directory moves can disappear when their resulting root trees are identical.

The extracted tip is `b45c3fa992cdab1da5cc4decca1bd7de70879879`.
Merge commit `9189761` joins the original starter history and extracted module history, adopting the Velox module files. Subsequent commits make the implementation independent and restore VG naming and coordinates.

## Commit mapping

| Original backend commit | Extracted commit | Original subject |
| --- | --- | --- |
| `558304038be0a537c6bd71f9b0181f8fa5b669d1` | `fca2e18fea816fe4e8e7127e4812e3f2929b2c17` | feat(email): add lightweight email module with SMTP support |
| `da04113902e3be8eced21c383d2b8d42b45d1371` | `84f13d4d12fafa6c470aadd50b73f548fe3f4a00` | refactor: remove Lombok dependency |
| `1d03266ca07df91282473333700414b96ff8cb91` | `51df96ecdafe23dbe7d27d77689eb8f867957f77` | refactor: Refactor email configuration and core components |
| `bbd7daae11db5587fbb6d7234947f02f75f7998f` | `fa90b3548ca0b2079d509cd66d22c90be72ff0ee` | refactor!: rename project from architecture-pro to velox-pro |
| `f069ca32f625649d45ed8121ab6265185a6a9574` | `7da0abe14e437a15e604fa1409fe4ed7cd0c7b49` | refactor!: remove old deprecated email and old user service interfaces |
| `4e4dcc49066229694b4e8b2156a5685ea972925b` | `e6d56074eeeaf2f356dcd0063f9e3b407469747e` | refactor!: flatten framework starters and decouple infra assembly from reusable capabilities |
| `69b4ab69a9a302e8e8dbc71f1c97437a0b6fe81f` | `d897f0303d41e4f655d2938a6e6d6150ef184b74` | refactor!: rename infra to bootstrap and collapse web assembly into framework starters |
| `cb4b817361f4efe9f845575f290ba47e3efee547` | `22e134a7efe02974a9e10c2b45b5d2c9d394d5d9` | refactor: rename spring boot starters |
| `17969e9ee4f7b726157955da6edf562616d34e41` | `a925f1ded1d1169e88daa6ede80a565f40fed5ed` | refactor: semantic restructuring of email starter module |
| `b01b33092d332c6bcf0c90dce6a6717f0be59ba0` | `79f1c05ab4b051ba7f89db3c89afc598387b364c` | refactor: restructure README files for Velox Email Spring Boot Starter |
| `1f3d6dcb446448418810d85a6bec972f14c96cbd` | `2f76cd4741dc80c291bbc1f8e409073252902902` | feat: add noop for email module |
| `ad6b538b3e4054079571ce5d90ae22dc5282514d` | `0f8e907e5cd772a2a05628aca1a4efadec739aac` | refactor: semanticize module structure and harden noop/logging/retry behavior |
| `8f84fbc15eb837a7bc652de5a3efa46b4c3995fe` | `7bd99c062bf73a4317ee61818858c30c40f07694` | refactor: Refactor property prefixes and configuration properties |
| `04e25d0417fba57c219de41b8295fd2e12f573cd` | `b45c3fa992cdab1da5cc4decca1bd7de70879879` | feat(auth): Introduce new authentication properties and refactor MFA handling |

## Independent implementation

- Maven coordinates: `io.github.virtualgemini:vg-lite-email-spring-boot-starter:1.0.0-SNAPSHOT`.
- Java package: `io.github.virtualgemini.vgliteemail`.
- Configuration prefix: `vg.lite-email`.
- No Velox parent POM or application modules are required.
- Build baseline: Java 21 and Spring Boot 3.4.5, inherited from the source dependencies with Java release reduced to the APIs actually used.
- Pure HTML MIME construction and custom canonical contract backoff were corrected and covered by tests.
- The existing Apache license is retained; the imported module's MIT notice is retained in `LICENSE-MIT` and the JAR.

## Inspecting history

```bash
git log --graph --oneline main
git merge-base --is-ancestor ad1da2884493a8c631a2ec222efc17ac71c5c1d1 main
git merge-base --is-ancestor b45c3fa992cdab1da5cc4decca1bd7de70879879 main
```

Both ancestry checks should succeed. The source commit mapping above allows comparison with the original backend history without bringing unrelated backend or frontend files into this repository.
