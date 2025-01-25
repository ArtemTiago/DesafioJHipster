import dayjs from 'dayjs/esm';

import { StatusCurso } from 'app/entities/enumerations/status-curso.model';

import { ICurso, NewCurso } from './curso.model';

export const sampleWithRequiredData: ICurso = {
  id: 11617,
  nome: 'Metal FTP extend',
  status: StatusCurso['ATIVO'],
  dataCriacao: dayjs('2025-01-25T10:33'),
};

export const sampleWithPartialData: ICurso = {
  id: 51737,
  nome: 'attitude-oriented',
  status: StatusCurso['INATIVO'],
  dataCriacao: dayjs('2025-01-24T22:58'),
  dataInatividade: dayjs('2025-01-24T22:29'),
};

export const sampleWithFullData: ICurso = {
  id: 6134,
  nome: 'wireless',
  descricao: 'Avon',
  status: StatusCurso['INATIVO'],
  dataCriacao: dayjs('2025-01-25T13:22'),
  dataInatividade: dayjs('2025-01-25T02:35'),
};

export const sampleWithNewData: NewCurso = {
  nome: 'Livros Balanced',
  status: StatusCurso['INATIVO'],
  dataCriacao: dayjs('2025-01-25T09:42'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
