
First time set up of local registry:
minikube start --memory=16384 --cpus=4 --insecure-registry localhost:5000 (or stop if its running already and start with insecure registry)
kubectl config use-context minikube
eval $(minikube docker-env)

Check after this that istio system is happy (may have to wait a few minutes). Assuming you have already installed istio in this minikube.
Get pods with: kubectl -n istio-system get pod
Debug with: kubectl logs <pod> -n istio-system -c <discovery or istio-proxy>

Also assuming here istio is already configured and sending traces to lightstep/working as expected with bookinfo or httpbin etc.

If you have an old registry running do this (otherwise skip)
kubectl delete -f ./yamls/local-registry.yaml

Then run:
kubectl apply -f ./yamls/local-registry.yaml

Test its running:
minikube ssh
curl localhost:5000/v2/_catalog
Should get {"repositories":[]}

Push a first image:
Run build-image.sh
Run push-image-local.sh
minikube ssh
curl localhost:5000/v2/_catalog
Should get {"repositories":["springio/gs-spring-boot-docker"]}

Set up networking on the image:
kubectl apply -f yamls/springapp.yaml
kubectl apply -f <(istioctl kube-inject -f yamls/springapp.yaml)

Get the address of the service:
export INGRESS_HOST=$(minikube ip)
export INGRESS_PORT=$(kubectl -n istio-system get service istio-ingressgateway -o jsonpath='{.spec.ports[?(@.name=="http2")].nodePort}')
curl http://$INGRESS_HOST:$INGRESS_PORT
should get Hello Docker World

Troubleshooting:
kubectl exec -it $(kubectl get pod -l app=spring-app -o jsonpath='{.items[0].metadata.name}') -c spring-app -- wget localhost:8080
kubectl exec -it $(kubectl get pod -l app=spring-app -o jsonpath='{.items[0].metadata.name}') -c spring-app -- cat index.html
Make sure this works and says Hello Docker World (means internally pod is running so the image is fine)
Note also all the ports istio is using by doing kubectl get service -n istio-system. If you change the ports in the yaml file don't pick a port istio owns already.


export LIGHTSTEP_ACCESS_TOKEN=425c9b9734e6cd039b41689aa83937cd
export LIGHTSTEP_SATELLITE_HOST=ingest.lightstep.com
export LIGHTSTEP_SATELLITE_PORT=80
export LIGHTSTEP_SECURE=false