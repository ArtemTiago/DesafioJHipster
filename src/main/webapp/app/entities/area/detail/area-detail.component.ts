import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IArea } from '../area.model';

@Component({
  selector: 'jhi-area-detail',
  templateUrl: './area-detail.component.html',
})
export class AreaDetailComponent implements OnInit {
  area: IArea | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ area }) => {
      this.area = area;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
