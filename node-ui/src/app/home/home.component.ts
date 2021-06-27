import { Component, OnInit } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { ToastrService } from 'ngx-toastr';

import { AuthService } from '../services/auth.service';
import { UserProviderService } from '../services/user-provider.service';

import { UserView } from '../models/user-view';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  username: string = '';
  password: string = '';

  constructor(private authService: AuthService,
              private userProvider: UserProviderService,
              private toastr: ToastrService) { }

  ngOnInit(): void {
  }

  omitSpecialChar(event: any) {
    let k = event.charCode;
    return((k > 64 && k < 91) || (k > 96 && k < 123) || k == 8 || k == 32 || (k >= 48 && k <= 57));
  }

  login(): void {
    this.authService.login(this.username, this.password).subscribe(
      (resp: HttpResponse<UserView>) => {
        if (resp.headers.get('Authorization')) {
          this.toastr.success('Login successful, welcome ${resp.body.username}');
        } else {
          this.toastr.error('Login unsuccessful');
        }
      },
      err => {
        this.toastr.error(err.message, 'Login unsuccessful');
      }
    );
  }
}
