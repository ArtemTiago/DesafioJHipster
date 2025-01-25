import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { AreaComponent } from './list/area.component';
import { AreaDetailComponent } from './detail/area-detail.component';
import { AreaUpdateComponent } from './update/area-update.component';
import { AreaDeleteDialogComponent } from './delete/area-delete-dialog.component';
import { AreaRoutingModule } from './route/area-routing.module';

@NgModule({
  imports: [SharedModule, AreaRoutingModule],
  declarations: [AreaComponent, AreaDetailComponent, AreaUpdateComponent, AreaDeleteDialogComponent],
})
export class AreaModule {}
