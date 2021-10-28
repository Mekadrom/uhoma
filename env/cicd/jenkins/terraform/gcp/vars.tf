variable "project_name" {
  type        = string
  description = "The name of the project to create/manage GCP resources in."
  default     = "root-furnace-306909"
}

variable "region" {
  type        = string
  description = "The region to create/manage GCP resources in."
  default     = "us-east1"
}

variable "zone" {
  type        = string
  description = "The zone to create/manage GCP resources in."
  default     = "us-east1-b"
}

variable "name" {
  type        = string
  description = "The name of the compute engine to create/manage."
  default     = "jenkinsmaster"
}

variable "type" {
  type        = string
  description = "The type of the compute engine to create/manage."
  default     = "e2-small"
}

variable "image" {
  type        = string
  description = "The image of the compute engine to create/manage."
  default     = "debian-cloud/debian-10"
}

variable "size" {
  type        = number
  description = "The size of the hard disk to create for the VM."
  default     = 10
}

variable "network_name" {
  type        = string
  description = "The name of the network in the GCP project to assign this compute engine to."
  default     = "default"
}

variable "sa_id" {
  default = ""
}

variable "sa_name" {
  default = ""
}