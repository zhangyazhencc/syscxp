# syscxp: the IaaS software you have been looking for. [http://syscxp.com](http://syscxp.com)

syscxp is open source IaaS(infrastructure as a service) software aiming to automate datacenters, managing resources of compute, storage,
and networking all by APIs. Users can setup syscxp environments in a download-and-run manner, spending 5 minutes building a POC environment
all on a single Linux machine, or 30 minutes building a multi-node production environment that can scale to hundreds of thousands of physical servers.

syscxp provides the capability of managing everything in a datacenter by APIs, fulfilling the goal of software-defined
datacenter. Users control their clouds using either web UI or command line tool both of which interact with syscxp
management nodes; **NO** scattered configurations, dependent software installation, services HA, and thirdparty monitoring
are needed, all of them are taken care of by syscxp itself, which provides a great simplicity for stable long-term operation.

## Why syscxp?

syscxp is designed to tackle two problems -- complexity and stability -- that users building clouds based on open source
IaaS software have been suffered for a long time.

In terms of complexity, syscxp sticks to the design principles of APIs managing everything, full automation, self-managed services,
no hardware lockin, and user-friendly query APIs, providing a software easy to setup and long-term operation.

In terms of stability, syscxp was born with a plugin system that adding or removing features will not impact existing codes,
a workflow engine that can rollback completed changes on error, a cascade framework that can spread an operation from
a resource to dependent resources, and three rigorous automated testing systems that guard every single feature,
solving the stability issue in architectural designs.

Besides, syscxp is extremely scalable that a single management node is capable of managing hundreds of thousands of
physical servers, managing millions of virtual machines, and serving tens of thousands of concurrent API requests,
particular suitable for building large-scale public clouds.

## Key Strength

#### Scalable

A single management node is capable of managing **hundreds of thousands** of physical servers, managing **millions** of virtual machines,
and serving **tens of thousands** of concurrent API requests.

#### Fast

Operations are **extremely fast**, see below performance data of creating VMs.

<table class="table table-bordered home-table" style="margin-bottom: 0;">
  <tr>
    <th>VM NUMBER</td>
    <th>TIME COST&nbsp;&nbsp;
        <i class='fa fa-info-circle' style='cursor:help' title="Limited by hardware, this data is from a mixed environment containing real VMs created on nested virtualization hypervisor and simulator VMs, which are created by 100 threads using only one management node. We are 100% sure the performance will get better in the real data center with decent hardware."></i>
    </td>
  </tr>
  <tr>
    <td>1</td>
    <td>0.51 seconds</td>
  </tr>
  <tr>
    <td>10</td>
    <td>1.55 seconds</td>
  </tr>
  <tr>
    <td>100</td>
    <td>11.33 seconds</td>
  </tr>
  <tr>
    <td>1000</td>
    <td>103 seconds</td>
  </tr>
  <tr>
    <td>10000</td>
    <td>23 minutes</td>
  </tr>
</table>

#### Network functions virtualization

The default networking model is built on **NFV(network functions virtualization)**, which provides every tenant a
dedicated networking node implemented by a virtual appliance VM. The whole networking model is self-contained and
self-managed, administrators need neither to purchase special hardware nor to deploy networking servers in front of
computing servers.

#### Comprehensive query APIs

Users can query everything everywhere by about **4,000,000** query conditions and <b>countless</b> query combinations.
You will never need to write ad-hoc scripts or directly access database to search a resource, it's all handled by APIs.

     >> QueryVmInstance vmNics.eip.guestIp=16.16.16.16 zone.name=west-coast
     
     >> QueryHost fields=name,uuid,managementIp hypervisorType=KVM vmInstance.allVolumes.size>=549755813888000 vmInstance.state=Running start=0 limit=10
     
#### Easy to deploy and upgrade

Installation and upgrade are as simple as deploying a **Java WAR file**. A POC environment can be installed in **5 minutes** with
a bootstrap script; A multi-node production environment can be deployed in **30 minutes** including the time you read the
documentation.

     >> [root@localhost ~]# curl http://download.syscxp.com/install.sh |  bash -s -- -a
     
#### Full automation

**Everything is managed by APIs**, no manual, scattered configurations in your cloud. And the seamless, transparent integration with
Ansible liberates you from installing, configuring, and upgrading agents on massive hardware.
 
#### Versatile plugin system

The core orchestration is built on an Eclipse and OSGI like **plugin system** that everything is a plugin. syscxp affirms
that adding or removing features will not impact the core orchestration, promising a robust software the cloud users deserve.

#### Rigorous testing system

**Three full-automated, rigorous testing systems** ensure the quality of every feature.


## Installation

Installation of syscxp is super easy; users can choose different methods depending on their needs to install the first
syscxp environment:

* For users wanting to try out quickly, see [Quick Installation](http://syscxp.com/installation/index.html).

* For users wanting to deploy a production environment, see [Manual Installation](http://syscxp.com/installation/manual.html).

* For users wanting to deploy a multi-node environment, see [Multi-node Installation](http://syscxp.com/installation/multi-node.html).

Once the installation is done, users can follow one of getting started guides:

* [Getting Started With Quick Installation](http://syscxp.com/documentation/getstart-quick.html).

* [Getting Started With Manual Installation](http://syscxp.com/documentation/getstart-manual.html).

* [Getting Started With Multi-node Installation](http://syscxp.com/documentation/getstart-multi.html).

## Tutorials

Six tutorials are prepared for your first journey in syscxp, building classic cloud deployments all on one single Linux machine:

##### Amazon EC2 classic EIP zone:

* [Web UI](http://syscxp.com/tutorials/ec2-ui.html)
* [Command Line Tool](http://syscxp.com/tutorials/ec2-cli.html)

##### Flat Network:

* [Web UI](http://syscxp.com/tutorials/flat-network-ui.html)
* [Command Line Tool](http://syscxp.com/tutorials/flat-network-cli.html)

##### Three Tiered Network:

* [Web UI](http://syscxp.com/tutorials/three-tiered-ui.html)
* [Command Line Tool](http://syscxp.com/tutorials/three-tiered-cli.html)

##### Security Group:

* [Web UI](http://syscxp.com/tutorials/security-group-ui.html)
* [Command Line Tool](http://syscxp.com/tutorials/security-group-cli.html)

##### Elastic Port Forwarding:

* [Web UI](http://syscxp.com/tutorials/elastic-port-forwarding-ui.html)
* [Command Line Tool](http://syscxp.com/tutorials/elastic-port-forwarding-cli.html)

##### Snapshots:

* [Web UI](http://syscxp.com/tutorials/snapshot-ui.html)
* [Command Line Tool](http://syscxp.com/tutorials/snapshot-cli.html)

## Under the hood 

Under the hood, syscxp is built on an architecture explained by following articles:

##### Scalability:

[syscxp's Scalability Secrets Part 1: Asynchronous Architecture](http://syscxp.com/blog/asynchronous-architecture.html)

[syscxp's Scalability Secrets Part 2: Stateless Services](http://syscxp.com/blog/stateless-clustering.html)

[syscxp's Scalability Secrets Part 3: Lock-free Architecture](http://syscxp.com/blog/lock-free.html)

##### Plugin Architecture:

[The In-Process Microservices Architecture](http://syscxp.com/blog/microservices.html)

[The Versatile Plugin System](http://syscxp.com/blog/plugin.html)

[The Tag System](http://syscxp.com/blog/tag.html)

[The Workflow Engine](http://syscxp.com/blog/workflow.html)

[The Cascade Framework](http://syscxp.com/blog/cascade.html)

##### Query API:

[The Query API](http://syscxp.com/blog/query.html)

##### Automation:

[Full Automation By Ansible](http://syscxp.com/blog/ansible.html)

##### Storage And Network:

[Networking Model 1: L2 and L3 Network](http://syscxp.com/blog/network-l2.html)

[Networking Model 2: Virtual Router Network Service Provider](http://syscxp.com/blog/virtual-router.html)

[Storage Model: Primary Storage and Backup Storage](http://syscxp.com/blog/storage.html)

##### Testing:

[The Automation Testing System 1: Integration Testing](http://syscxp.com/blog/integration-testing.html)

[The Automation Testing System 2: System Testing](http://syscxp.com/blog/system-testing.html)

[The Automation Testing System 3: Model-based Testing](http://syscxp.com/blog/model-based-testing.html)

## License

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
except in compliance with the License. You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is
distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing roleSet and limitations under the License.


  
