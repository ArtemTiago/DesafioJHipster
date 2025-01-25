import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IArea } from '../area.model';
import { AreaService } from '../service/area.service';

@Injectable({ providedIn: 'root' })
export class AreaRoutingResolveService implements Resolve<IArea | null> {
  constructor(protected service: AreaService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IArea | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((area: HttpResponse<IArea>) => {
          if (area.body) {
            return of(area.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
