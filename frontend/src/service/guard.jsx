// src/ProtectedRoute.js
import React from "react";
import { Navigate, useLocation } from "react-router-dom";
import ApiService from "./ApiService";

export const ProtectedRoute = ({ children }) => {
  const location = useLocation();

  return ApiService.isAuthenticated() ? (
    children
  ) : (
    <Navigate to="/login" replace state={{ from: location }} />
  );
};

export const AdminRoute = ({ children }) => {
  const location = useLocation();

  return ApiService.isAdmin() ? (
    children
  ) : (
    <Navigate to="/login" replace state={{ from: location }} />
  );
};
