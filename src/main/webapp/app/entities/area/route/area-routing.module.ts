import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { AreaComponent } from '../list/area.component';
import { AreaDetailComponent } from '../detail/area-detail.component';
import { AreaUpdateComponent } from '../update/area-update.component';
import { AreaRoutingResolveService } from './area-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const areaRoute: Routes = [
  {
    path: '',
    component: AreaComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: AreaDetailComponent,
    resolve: {
      area: AreaRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: AreaUpdateComponent,
    resolve: {
      area: AreaRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: AreaUpdateComponent,
    resolve: {
      area: AreaRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(areaRoute)],
  exports: [RouterModule],
})
export class AreaRoutingModule {}
