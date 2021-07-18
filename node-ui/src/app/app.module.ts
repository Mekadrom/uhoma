import { NgModule } from '@angular/core';
import { ErrorHandler } from "@angular/core";
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';

import { MatListModule } from '@angular/material/list';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

import { ToastrModule } from 'ngx-toastr';
import { CookieService } from 'ngx-cookie-service';

import { AppRoutingModule } from './app-routing.module';

import { AppComponent } from './app.component';
import { NodeComponent } from './node/node.component';
import { HomeComponent } from './home/home.component';
import { DevComponent } from './dev/dev.component';
import { SettingsComponent } from './settings/settings.component';
import { NodeActionComponent } from './node-action/node-action.component';
import { ActionComponent } from './action/action.component';

import { AuthInterceptorService } from './services/auth-interceptor.service';
import { TypedEditableTableComponent } from './typed-editable-table/typed-editable-table.component';

@NgModule({
  declarations: [
    AppComponent,
    NodeComponent,
    HomeComponent,
    DevComponent,
    SettingsComponent,
    NodeActionComponent,
    ActionComponent,
    TypedEditableTableComponent
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
    MatTableModule,
    MatProgressSpinnerModule,
    ToastrModule.forRoot()
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptorService,
      multi: true
    },
    CookieService
  ],
  bootstrap: [ AppComponent ]
})
export class AppModule { }
