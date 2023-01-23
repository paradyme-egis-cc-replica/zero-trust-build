# zerotrust-build

# Note that this repository must be exposed via github pages to make the files accessible to Jenkins to download the groovy files.

Holds the Jenkins configuration for the zero-trust example application.

Used to load the application into Jenkins.
Initially, load the [zero-trust-jenkins.yaml](zero-trust-jenkins.yaml) into the Jenkins Configuration-as-Code.

## Installation

1. Secret configuration to access github and AWS.
   1. Configure the github credentials used by Jenkins to fetch the source code ([see below.](#example-of-how-to-create-github-idyaml))
   1. kubectl apply -f github-id.yaml
   1. Configure the personal access token used to configure the repositories ([see below.](#example-of-how-to-create-github-patyaml))
   1. kubectl apply -f github-pat.yaml
   1. Configure the AWS credentials used to push images into ECR ([see below.](#example-of-how-to-create-aws-credentialsyaml))
   1. kubectl apply -f aws-credentials.yaml
   1. Configure the github credentials used by ArgoCD to fetch the deployment code ([see below.](#example-of-how-to-create-argocd-github-idyaml))
   1. kubectl apply -f argocd-github-id.yaml
1. Log into Jenkins
1. Navigate to Dashboard -> Manage Jenkins -> Manage Plugins
   1. Update the Kubernetes plugin to the latest version
   1. Update any additional plugins as desired (recommend installing any that address security vulnerabilities.)
   1. Download the updates, install, and reboot the Jenkins server.
   1. Wait for the Jenkins server to restart completely.
1. Naviage to Dashboard -> Manage Jenkins -> Configuration as Code
   1. Specify `https://paradyme-egis-cc-replica.github.io/zero-trust-build/zero-trust-jenkins.yaml` as the URL.
   1. Click "Apply new configuration".

The build will be triggered and the zero-trust demonstration code deployed.

## Example of how to create github-id.yaml

```
cat > github-id.yaml <<EOF
apiVersion: v1
kind: Secret
type: Opaque
metadata:
  annotations:
    jenkins.io/credentials-description: github access
  labels:
    jenkins.io/credentials-type: usernamePassword
  name: github-id
  namespace: cicd
data:
  username: <<< Base64 encoded username here. >>>
  password: <<< Base64 encoded personal access token here. >>>
EOF
```

## Example of how to create github-pat.yaml

```
cat > github-pat.yaml <<EOF
apiVersion: v1
kind: Secret
type: Opaque
metadata:
  annotations:
    jenkins.io/credentials-description: github access
  labels:
    jenkins.io/credentials-type: secretText
  name: github-pat
  namespace: cicd
data:
  text: <<< Base64 encoded personal access token here. >>>
EOF
```


## Example of how to create aws-credentials.yaml

```
cat > aws-credentials.yaml <<EOF
apiVersion: v1
kind: Secret
type: Opaque
metadata:
  annotations:
    jenkins.io/credentials-description: Credentials to access AWS
  labels:
    jenkins.io/credentials-type: usernamePassword
  name: aws-credentials
  namespace: cicd
data:
  username: <<< aws_access_key_id base64 encoded >>>
  password: <<< aws_secret_access_key base64 encoded >>>
EOF
```

## Example of how to create argocd-github-id.yaml

```
cat > argocd-github-id.yaml <<EOF
apiVersion: v1
kind: Secret
type: Opaque
metadata:
  annotations:
    managed-by: argocd.argoproj.io
  labels:
    argocd.argoproj.io/secret-type: repo-creds
  name: github-credentials-argocd
  namespace: cicd
data:
  username: <<< Base64 encoded username here. >>>
  password: <<< Base64 encoded personal access token here. >>>
  url: aHR0cHM6Ly9naXRodWIuY29tL3BhcmFkeW1lLW1hbmFnZW1lbnQ=
EOF
```
