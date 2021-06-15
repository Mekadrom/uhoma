import { NgModule } from '@angular/core';
import { ErrorHandler } from "@angular/core";
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule } from '@angular/common/http';
import { MatListModule } from '@angular/material/list';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatSelectModule } from '@angular/material/select';
import { ToastrModule } from 'ngx-toastr';
import { AppRoutingModule } from './app-routing.module';

import { AppComponent } from './app.component';
import { NodeComponent } from './node/node.component';
import { HomeComponent } from './home/home.component';
import { DevComponent } from './dev/dev.component';
import { AdminComponent } from './admin/admin.component';
import { SettingsComponent } from './settings/settings.component';
import { NodeActionComponent } from './node-action/node-action.component';
import { ActionComponent } from './action/action.component';

@NgModule({
  declarations: [
    AppComponent,
    NodeComponent,
    HomeComponent,
    DevComponent,
    AdminComponent,
    SettingsComponent,
    NodeActionComponent,
    ActionComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    BrowserAnimationsModule,
    HttpClientModule,
    MatListModule,
    MatGridListModule,
    MatSelectModule,
    ToastrModule.forRoot()
  ],
  providers: [  ],
  bootstrap: [ AppComponent ]
})
export class AppModule { }
