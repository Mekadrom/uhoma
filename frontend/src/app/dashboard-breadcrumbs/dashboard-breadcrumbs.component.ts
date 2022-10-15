import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

import { ToastrService } from 'ngx-toastr';

import { NewHomeDialogComponent } from '../new-home-dialog/new-home-dialog.component';
import { DashboardBreadCrumb } from '../models';
import { HomeService } from '../services';

@Component({
  selector: 'app-dashboard-breadcrumbs',
  templateUrl: './dashboard-breadcrumbs.component.html',
  styleUrls: ['./dashboard-breadcrumbs.component.scss']
})
export class DashboardBreadcrumbsComponent implements OnInit {
  breadcrumbData?: DashboardBreadCrumb;

  newHomeName: string = '';
  homeSearchCriteria?: string = '';

  @Output() public changeHomeSelectionEmitter: EventEmitter<string> = new EventEmitter<string>();
  @Input() public breadcrumbEmitter?: EventEmitter<DashboardBreadCrumb>;

  constructor(private dialog: MatDialog,
              private homeService: HomeService,
              private toastr: ToastrService) { }

  ngOnInit(): void {
    this.breadcrumbEmitter?.subscribe((e: DashboardBreadCrumb) => {
      this.breadcrumbData = e;
      this.homeSearchCriteria = this.breadcrumbData.home;
      console.debug(JSON.stringify(this.breadcrumbData));
    });
  }

  createNewHomeDialog(): void {
    const dialogRef = this.dialog.open(NewHomeDialogComponent, {
      width: '250px',
      data: this.newHomeName,
    });

    dialogRef.afterClosed().subscribe((result: string) => {
      if (result) {
        this.createNewHome(result);
      } else {
        if (this.breadcrumbData && this.breadcrumbData.homes) {
          this.homeSearchCriteria = this.breadcrumbData.homes[0];
          this.changeHomeSelection();
        }
      }
    });
  }

  createNewHome(name: string): void {
    if (name) {
      this.homeService.createNewHome(name).subscribe(
        (resp: any) => {
          this.homeSearchCriteria = name;
          this.changeHomeSelection();
          this.toastr.success('Home created successfully');
        },
        (err: any) => {
          this.toastr.error(err.message, 'New home creation failed');
        }
      );
    }
  }

  changeHomeSelection(): void {
    if (this.homeSearchCriteria) {
      console.debug('changeHomeSelectionEmitting: ' + this.homeSearchCriteria);
      this.changeHomeSelectionEmitter.emit(this.homeSearchCriteria);
    }
  }
}
