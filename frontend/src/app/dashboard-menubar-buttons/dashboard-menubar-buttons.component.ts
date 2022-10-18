import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
  selector: 'app-dashboard-menubar-buttons',
  templateUrl: './dashboard-menubar-buttons.component.html',
  styleUrls: ['./dashboard-menubar-buttons.component.scss']
})
export class DashboardMenubarButtonsComponent implements OnInit {
  @Output() saveCallback: EventEmitter<any> = new EventEmitter();
  @Output() resetAllCallback: EventEmitter<any> = new EventEmitter();
  @Output() executeActionCallback: EventEmitter<any> = new EventEmitter();

  @Input() selectedActionRowEmitter?: EventEmitter<number>;

  selectedActionRow: number = -1;

  constructor() { }

  ngOnInit(): void {
    this.selectedActionRowEmitter?.subscribe((e: number) => {
      this.selectedActionRow = e;
    });
  }

  save(): void {
    this.saveCallback.emit();
  }

  resetAll(): void {
    this.resetAllCallback.emit();
  }

  executeAction(): void {
    this.executeActionCallback.emit();
  }
}
