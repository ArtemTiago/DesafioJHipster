import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AreaDetailComponent } from './area-detail.component';

describe('Area Management Detail Component', () => {
  let comp: AreaDetailComponent;
  let fixture: ComponentFixture<AreaDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AreaDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ area: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(AreaDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(AreaDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load area on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.area).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
