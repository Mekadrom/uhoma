provider "google" {
  project = var.project_name
  region  = var.region
  zone    = var.zone
}

resource "google_compute_instance" "vm" {
  name         = var.name
  machine_type = var.type
  zone         = var.zone

  boot_disk {
    initialize_params {
      image = var.image
      size  = var.size
    }
  }

  network_interface {
    network = var.network_name
  }
}
