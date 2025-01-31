# MApp Helm Chart

![Version: 0.0.1](https://img.shields.io/badge/Version-0.0.1-informational?style=flat-square) ![AppVersion: 0.0.1](https://img.shields.io/badge/AppVersion-0.0.1-informational?style=flat-square) ![Type: application](https://img.shields.io/badge/Type-application-informational?style=flat-square)

A simple Micronaut application demonstrating a cloud native Microservices architecture for journal logging, audit logging, metrics collection, and tracing.

## Maintainers

| Name | Email | Url |
| ---- | ------ | --- |
| tglaeser | <thomas.glaeser@softwareag.com> | <https://github.softwareag.com/tglaeser> |

## Requirements

| Repository | Name | Version |
|------------|------|---------|
| oci://registry-1.docker.io/bitnamicharts | common | 2.14.1 |
| oci://registry-1.docker.io/bitnamicharts | elasticsearch | 19.13.15 |
| oci://registry-1.docker.io/bitnamicharts | fluentd | 5.11.0 |
| oci://registry-1.docker.io/bitnamicharts | kibana | 10.6.7 |

## Usage

> Note: The following examples assume that all commands are executed at the root directory of this source repository.

#### System chart
The system chart describes the installation of the overall system.

###### Check chart
```shell
$ ./scripts/helm-dep-build.sh ./charts/mapp
$ helm lint ./charts/mapp
$ helm install mapp-system ./charts/mapp --dry-run --debug
$ ./scripts/kube-val.sh ./charts/mapp
```

###### Install chart
```shell
$ kubectl create namespace mapp-test
$ helm --namespace mapp-test upgrade --install mapp ./charts/mapp
```

###### Uninstall chart
```shell
$ helm --namespace mapp-test uninstall mapp
$ kubectl delete namespace mapp-test
```

## Values

| Key | Type | Default | Description |
|-----|------|---------|-------------|
| fluentd.aggregator.configMap | string | `"mapp-fluentd-aggregator-cm"` |  |
| fluentd.forwarder.configMap | string | `"mapp-fluentd-forwarder-cm"` |  |
| fluentd.forwarder.service.ports.http-app.port | int | `8080` |  |
| fluentd.forwarder.service.ports.http-app.protocol | string | `"TCP"` |  |
| fluentd.forwarder.service.ports.http-app.targetPort | string | `"http-app"` |  |
| fluentd.forwarder.service.ports.http.port | int | `9880` |  |
| fluentd.forwarder.service.ports.http.protocol | string | `"TCP"` |  |
| fluentd.forwarder.service.ports.http.targetPort | string | `"http"` |  |
| fluentd.forwarder.sidecars[0].image | string | `"local/mapp-native:0.0.1"` |  |
| fluentd.forwarder.sidecars[0].imagePullPolicy | string | `"IfNotPresent"` |  |
| fluentd.forwarder.sidecars[0].name | string | `"mapp"` |  |
| fluentd.forwarder.sidecars[0].ports[0].containerPort | int | `8080` |  |
| fluentd.forwarder.sidecars[0].ports[0].name | string | `"http-app"` |  |
| kibana.elasticsearch.hosts[0] | string | `"mapp-elasticsearch.mapp-test.svc.cluster.local"` |  |
| kibana.elasticsearch.port | int | `9200` |  |

----------------------------------------------
Autogenerated from chart metadata using [helm-docs v1.12.0](https://github.com/norwoodj/helm-docs/releases/v1.12.0)
