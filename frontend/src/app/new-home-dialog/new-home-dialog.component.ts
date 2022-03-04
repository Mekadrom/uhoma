import { Inject, Component, OnInit } from '@angular/core';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-new-home-dialog',
  templateUrl: './new-home-dialog.component.html',
  styleUrls: ['./new-home-dialog.component.scss']
})
export class NewHomeDialogComponent {
  constructor(public dialogRef: MatDialogRef<NewHomeDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public newHomeName: String) {}

  onNoClick(): void {
    this.dialogRef.close();
  }
}
