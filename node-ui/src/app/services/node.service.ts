import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Node } from '../models/node';
import { Observable, throwError } from 'rxjs';
import { Injectable } from '@angular/core';
import { retry, catchError } from 'rxjs/operators';
import { HttpResponse } from "@angular/common/http";
import { ToastrService } from 'ngx-toastr';

@Injectable({
  providedIn: 'root'
})
export class NodeService {
  constructor(private http: HttpClient, private toastr: ToastrService) {}

  getNodes(nodeName: string | null, roomName: string | null): Observable<Node[]> {
    return this.http.get<Node[]>(this.getNodeSearchUrl(nodeName, roomName))
    .pipe(
      retry(1),
      catchError(this.handleError)
    )
  }

  getNodeSearchUrl(nodeName: string | null, roomName: string | null): string {
    var searchUrl: string = 'http://localhost:8080/node/search';

    if (nodeName != null && roomName == null && !nodeName.trim()) {
      searchUrl = searchUrl + '?name=' + nodeName;
    }

    if (nodeName == null && roomName != null && !roomName.trim()) {
      searchUrl = searchUrl + '?room=' + roomName;
    }

    if (nodeName != null && roomName != null) {
      searchUrl = searchUrl + '?name=' + nodeName + '&room=' + roomName
    }

    console.log(searchUrl)
    return searchUrl;
  }

  handleError(error: any): Observable<any> {
    return throwError(error);
  }
}
