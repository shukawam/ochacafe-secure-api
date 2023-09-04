# OCHaCafe - Secure Web API(cert-manager and Keycloak)

Contains sample and demo code used in [OCHaCafe Season7 #4 - How to make Secure Web API](https://ochacafe.connpass.com/event/289546/).

```bash
.
├── cert-manager
│   └── issuer # cert-manager Cluster Issuer のサンプル
├── helidon-rp # Helidon による OpenID Connect - RP のサンプル実装（Security Provider - OIDC Provider を使用）
│   ├── k8s # Kubernetes 関連ファイル
│   └── src # アプリケーションの実装
├── img
└── keycloak # Keycloak 関連ファイル
    ├── k8s # # Kubernetes 関連ファイル
    └── realm-settings # Pulumi による Keycloak - Realm の設定例
```

## cert-manager

### Before you start

The following contents have already done.

- [Oracle Container Engine for Kubernetes(OKE)をプロビジョニングしよう](https://oracle-japan.github.io/ocitutorials/cloud-native/oke-for-commons/)
- [例: クラスタでの Nginx イングレス・コントローラの設定](https://docs.oracle.com/ja-jp/iaas/Content/ContEng/Tasks/contengsettingupingresscontroller.htm)

### Install cert-manager

Add the Helm repository.

```bash
helm repo add jetstack https://charts.jetstack.io
```

Update your local Helm chart repository cache.

```bash
helm repo update
```

Install cert-manager and CRDs as part of the Helm release.

```bash
helm install \
  cert-manager jetstack/cert-manager \
  --namespace cert-manager \
  --create-namespace \
  --version v1.12.0 \
  --set installCRDs=true
```

### Create Cluster Issuer

Configureing the HTTP01 Ingress solver.

```bash
export YOUR_EMAIL=<your-email>
```

For staging env.

```bash
cat > cert-manager/issuer/letsencrypt-staging.yaml << EOF
apiVersion: cert-manager.io/v1
kind: ClusterIssuer
metadata:
  name: letsencrypt-staging
  namespace: cert-manager
spec:
  acme:
    server: https://acme-staging-v02.api.letsencrypt.org/directory
    email: $YOUR_EMAIL
    privateKeySecretRef:
      name: letsencrypt-staging
    solvers:
      - http01:
          ingress:
            class: nginx
EOF
```

For production env.

```bash
cat > cert-manager/issuer/letsencrypt-prod.yaml << EOF
apiVersion: cert-manager.io/v1
kind: ClusterIssuer
metadata:
  name: letsencrypt-prod
  namespace: cert-manager
spec:
  acme:
    server: https://acme-v02.api.letsencrypt.org/directory
    email: $YOUR_EMAIL
    privateKeySecretRef:
      name: letsencrypt-prod
    solvers:
      - http01:
          ingress:
            class: nginx
EOF
```

Apply letsencrypt-staging.yaml and letsencrypt-prod.yaml to your cluster.

```bash
kubectl apply -f cert-manager/issuer/letsencrypt-staging.yaml \
  -f cert-manager/issuer/letsencrypt-prod.yaml
```

## Keycloak

### Install keycloak

Install Keycloak and MySQL.

```bash
kubectl apply -k keycloak/k8s
```

### Set up Pulumi for Keycloak settings

Refer to this [page](https://www.pulumi.com/docs/install/) and install Pulumi.

### Setting Keycloak

Set google provider's client-id and client-secret

```bash
export GOOGLE_PROVIDER_CLIENT_ID=<YOUR_CLIENT_ID>
export GOOGLE_PROVIDER_CLIENT_SECRET=<YOUR_CLIENT_SECRET>
pulumi config set realm-settings:data.realms[0].googleProvider.clientId $GOOGLE_PROVIDER_CLIENT_ID --secret
pulumi config set realm-settings:data.realms[0].googleProvider.clientSecret $GOOGLE_PROVIDER_CLIENT_SECRET --secret
```

Optional: Set oidc-rp client-secret

```bash
export OIDC_RP_CLIENTE_SECRET=<YOUR_CLIENT_SECRET>
pulumi config set realm-settings:data.realms[0].oidc.clients[1].clientSecret $OIDC_RP_CLIENTE_SECRET --secret
```

Exec `pulumi preview | up`

```bash
pulumi up
```
